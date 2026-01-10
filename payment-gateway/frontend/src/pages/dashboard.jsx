import { useEffect, useState } from "react";
import api from "../services/api";
import Navbar from "../components/Navbar";

export default function Dashboard() {
  const [stats, setStats] = useState({
    totalTransactions: 0,
    totalAmount: 0,
    successRate: 0
  });

  useEffect(() => {
    api.get("/dashboard", {
      headers: {
        "X-Api-Key": localStorage.getItem("apiKey"),
        "X-Api-Secret": localStorage.getItem("apiSecret")
      }
    }).then(res => setStats(res.data));
  }, []);

  return (
    <>
      <Navbar />

      <div className="page-container" data-test-id="dashboard">
        <div className="card">
          <p><b>API Key:</b> {localStorage.getItem("apiKey")}</p>
          <p><b>API Secret:</b> {localStorage.getItem("apiSecret")}</p>
        </div>

        <div className="stats">
          <div className="stat-card">
            {stats.totalTransactions}
            <span>Total Transactions</span>
          </div>

          <div className="stat-card">
            ₹{stats.totalAmount / 100}
            <span>Total Amount</span>
          </div>

          <div className="stat-card">
            {stats.successRate}%
            <span>Success Rate</span>
          </div>
        </div>
      </div>
    </>
  );
}
