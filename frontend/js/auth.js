import { initializeApp } from "https://www.gstatic.com/firebasejs/10.10.0/firebase-app.js";
import { getAuth, signInWithEmailAndPassword, createUserWithEmailAndPassword, updateProfile, signInWithPopup, GoogleAuthProvider } from "https://www.gstatic.com/firebasejs/10.10.0/firebase-auth.js";

const firebaseConfig = {
  projectId: "YOUR_PROJECT_ID",
  appId: "YOUR_APP_ID",
  storageBucket: "YOUR_STORAGE_BUCKET",
  apiKey: "YOUR_API_KEY",
  authDomain: "YOUR_AUTH_DOMAIN",
  messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
  projectNumber: "YOUR_PROJECT_NUMBER",
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

const API = 'http://localhost:8080/api';

/* ---- Tab Switching ---- */
window.switchTab = function(tab) {
  document.getElementById('loginTab').classList.toggle('active', tab === 'login');
  document.getElementById('signupTab').classList.toggle('active', tab === 'signup');
  document.getElementById('loginForm').style.display  = tab === 'login'  ? 'block' : 'none';
  document.getElementById('signupForm').style.display = tab === 'signup' ? 'block' : 'none';
  clearErrors();
}

/* ---- Login ---- */
window.doLogin = async function() {
  const email    = document.getElementById('loginEmail').value.trim();
  const password = document.getElementById('loginPassword').value.trim();
  if (!email || !password) { showError('Please fill in all fields.'); return; }

  setLoading('loginBtn', true);
  try {
    const userCredential = await signInWithEmailAndPassword(auth, email, password);
    await finalizeLogin(userCredential.user);
  } catch (err) {
    showError(err.message || 'Login failed.');
  } finally {
    setLoading('loginBtn', false);
  }
}

/* ---- Signup ---- */
window.doSignup = async function() {
  const name     = document.getElementById('signupName').value.trim();
  const email    = document.getElementById('signupEmail').value.trim();
  const phone    = document.getElementById('signupPhone').value.trim();
  const password = document.getElementById('signupPassword').value.trim();

  if (!name || !email || !phone || !password) { showError('Please fill in all fields.'); return; }
  if (password.length < 6) { showError('Password must be at least 6 characters.'); return; }

  setLoading('signupBtn', true);
  try {
    const userCredential = await createUserWithEmailAndPassword(auth, email, password);
    await updateProfile(userCredential.user, { displayName: name });
    window.showToast('Account created successfully!', 'success');
    await finalizeLogin(userCredential.user, phone);
  } catch (err) {
    showError(err.message || 'Signup failed.');
  } finally {
    setLoading('signupBtn', false);
  }
}

/* ---- Google Login ---- */
window.doGoogleLogin = async function() {
  try {
    const userCredential = await signInWithPopup(auth, googleProvider);
    await finalizeLogin(userCredential.user);
  } catch(err) {
    showError(err.message || 'Google Auth Failed.');
  }
}

/* ---- Finalize Auth State ---- */
async function finalizeLogin(firebaseUser, phoneNum = null) {
  const token = await firebaseUser.getIdToken();
  const userData = {
    uid: firebaseUser.uid,
    user_id: 1, // Mock mapping for internal database
    name: firebaseUser.displayName || 'User',
    email: firebaseUser.email,
    phone: phoneNum || firebaseUser.phoneNumber || '',
    token: token
  };
  sessionStorage.setItem('user', JSON.stringify(userData));
  showSuccess();
}


/* ---- Success + Redirect ---- */
function showSuccess() {
  document.getElementById('loginForm').style.display  = 'none';
  document.getElementById('signupForm').style.display = 'none';
  document.getElementById('authTabs').style.display   = 'none';
  document.getElementById('successScreen').classList.add('visible');

  // Animate progress bar
  let progress = 0;
  const bar = document.getElementById('redirectProgress');
  const timer = setInterval(() => {
    progress += 4;
    bar.style.width = progress + '%';
    if (progress >= 100) {
      clearInterval(timer);
      window.location.href = 'dashboard.html';
    }
  }, 60);
}

/* ---- Helpers ---- */
function showError(msg) {
  const el = document.getElementById('authError');
  el.textContent = '⚠️ ' + msg;
  el.classList.add('visible');
}

function clearErrors() {
  document.getElementById('authError').classList.remove('visible');
}

function setLoading(btnId, loading) {
  const btn = document.getElementById(btnId);
  btn.classList.toggle('loading', loading);
  btn.disabled = loading;
}

window.showToast = function(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <span>${type === 'success' ? '✅' : type === 'error' ? '❌' : type === 'warning' ? '⚠️' : 'ℹ️'}</span>
    <span>${message}</span>
  `;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 4000);
};

/* ---- Init ---- */
(function init() {
  const user = sessionStorage.getItem('user');
  if (user) window.location.href = 'dashboard.html';
})();
