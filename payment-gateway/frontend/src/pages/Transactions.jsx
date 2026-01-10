import { useEffect, useState } from "react";
import api from "../services/api";
import Navbar from "../components/Navbar";

export default function Transactions() {
  const [payments, setPayments] = useState([]);

  useEffect(() => {
    api.get("/payments", {
      headers: {
        "X-Api-Key": localStorage.getItem("apiKey"),
        "X-Api-Secret": localStorage.getItem("apiSecret")
      }
    }).then(res => setPayments(res.data));
  }, []);

  return (
    <>
      <Navbar />

      <div className="page-container">
        <div className="table-card">
          <div className="table-header">
            <span>Payment ID</span>
            <span>Order ID</span>
            <span>Amount</span>
            <span>Method</span>
            <span>Status</span>
            <span>Created</span>
          </div>

          {payments.map(p => (
            <div key={p.id} className="table-row">
              <span>{p.id}</span>
              <span>{p.order_id}</span>
              <span>₹{p.amount / 100}</span>
              <span>{p.method}</span>
              <span className={p.status}>{p.status}</span>
              <span>{new Date(p.created_at).toLocaleString()}</span>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}
