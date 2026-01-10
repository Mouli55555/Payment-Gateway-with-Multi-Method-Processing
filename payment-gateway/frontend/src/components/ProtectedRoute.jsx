import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children }) {
  const loggedIn = localStorage.getItem("merchant_email");
  return loggedIn ? children : <Navigate to="/login" />;
}
