import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const submit = (e) => {
    e.preventDefault();

    if (email === "test@example.com") {
      localStorage.setItem("merchant_email", email);
      localStorage.setItem("api_key", "key_test_abc123");
      localStorage.setItem("api_secret", "secret_test_xyz789");
      navigate("/dashboard");
    } else {
      alert("Invalid email");
    }
  };

  return (
    <div className="center-card">
      <form data-test-id="login-form" onSubmit={submit} className="card">
        <h2>Merchant Login</h2>

        <input
          data-test-id="email-input"
          type="email"
          placeholder="Email"
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          data-test-id="password-input"
          type="password"
          placeholder="Password"
          onChange={(e) => setPassword(e.target.value)}
        />

        <button data-test-id="login-button">Login</button>
      </form>
    </div>
  );
}
