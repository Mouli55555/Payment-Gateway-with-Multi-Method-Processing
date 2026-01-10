import { BrowserRouter, Routes, Route } from "react-router-dom";
import Checkout from "./pages/Checkout";
import Success from "./pages/Success";
import Failure from "./pages/Failure";
import "./App.css";

function MissingOrder() {
  return (
    <div className="center-card">
      <div className="card">
        <h2>Invalid Checkout Link</h2>
        <p>Order ID is missing.</p>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <nav className="navbar">
        <h1>Partnr</h1>
      </nav>

      <Routes>
        {/* Checkout requires order_id */}
        <Route
          path="/"
          element={
            window.location.search.includes("order_id=")
              ? <Checkout />
              : <MissingOrder />
          }
        />

        <Route path="/success" element={<Success />} />
        <Route path="/failure" element={<Failure />} />
      </Routes>
    </BrowserRouter>
  );
}
