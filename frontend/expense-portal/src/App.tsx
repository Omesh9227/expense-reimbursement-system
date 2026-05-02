import { Navigate, NavLink, Route, Routes, useNavigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import EmployeeDashboard from "./pages/EmployeeDashboard";
import ManagerDashboard from "./pages/ManagerDashboard";
import { getUser, logout } from "./auth/auth";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function Header() {
  const user = getUser();
  const nav = useNavigate();

  return (
    <header className="header" style={{ position: "sticky", top: 0, zIndex: 50 }}>
      <div className="header-inner container">
        <div style={{ fontSize: 23, fontWeight: 700, letterSpacing: 0.3 }}>Expense Portal</div>

        {user && (
          <nav className="nav">
            {user.role === "EMPLOYEE"}
            {user.role === "MANAGER"}
          </nav>
        )}

        <div className="spacer" />

        {user ? (
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <span className={`badge ${user.role}`}>{user.role}</span>
            <button
              className="btn ghost"
              onClick={() => {
                logout();
                //nav("/login", { replace: true });
              }}
              aria-label="Logout"
            >
              Logout
            </button>
          </div>
        ) : (
          <NavLink to="/login" className="btn">
            Login
          </NavLink>
        )}
      </div>
    </header>
  );
}

export default function App() {
  const user = getUser();

  return (
    <>
      <Header />
      <div className="container" style={{ paddingTop: 16 }}>
        <Routes>
          {/* Root redirect */}
          <Route
            path="/"
            element={
              user ? (
                <Navigate to={user.role === "MANAGER" ? "/manager" : "/employee"} replace />
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />

          {/* Auth */}
          <Route
            path="/login"
            element={
              user ? (
                <Navigate to={user.role === "MANAGER" ? "/manager" : "/employee"} replace />
              ) : (
                <Login />
              )
            }
          />
          <Route
            path="/register"
            element={
              user ? (
                <Navigate to={user.role === "MANAGER" ? "/manager" : "/employee"} replace />
              ) : (
                <Register />
              )
            }
          />

          {/* Employee */}
          <Route
            path="/employee"
            element={
              <ProtectedRoute roles={["EMPLOYEE"]}>
                <EmployeeDashboard />
              </ProtectedRoute>
            }
          />

          {/* Manager */}
          <Route
            path="/manager"
            element={
              <ProtectedRoute roles={["MANAGER"]}>
                <ManagerDashboard />
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<div className="card">Not Found</div>} />
        </Routes>
      </div>

      {/* Global toasts */}
      <ToastContainer position="top-right" autoClose={4000} newestOnTop theme="light" />
    </>
  );
}
