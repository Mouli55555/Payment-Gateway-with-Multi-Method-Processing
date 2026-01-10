import { useEffect, useState } from "react";
import api from "../services/api";
import PaymentButton from "../components/PaymentButton";

export default function Checkout() {
  // const orderId = new URLSearchParams(window.location.search).get("order_id");

  const [order, setOrder] = useState(null);
  const [method, setMethod] = useState(null);
  const [paymentId, setPaymentId] = useState(null);

  const [processing, setProcessing] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState(false);

const params = new URLSearchParams(window.location.search);
const orderId = params.get("order_id");

useEffect(() => {
  if (!orderId) {
    console.error("Missing order_id in URL");
    setError(true);
    return;
  }

  api.get(`/orders/public/${orderId}`)
    .then(res =>{console.log(res) ;setOrder(res.data)})
    .catch(err =>{console.log(err) ;setError(true)});

}, [orderId]);

  useEffect(() => {
    if (!paymentId) return;

    const poll = setInterval(() => {
      api.get(`/payments/public/${paymentId}`).then(res => {
        if (res.data.status === "SUCCESS") {
          setProcessing(false);
          setSuccess(true);
          clearInterval(poll);
        }
        if (res.data.status === "FAILED") {
          setProcessing(false);
          setError(true);
          clearInterval(poll);
        }
      });
    }, 2000);

    return () => clearInterval(poll);
  }, [paymentId]);

  const pay = (payload) => {
    setProcessing(true);
    api.post("/payments/public", payload)
      .then(res => setPaymentId(res.data.id))
      .catch(() => setError(true));
  };

  if (!order) return null;

  return (
    <div data-test-id="checkout-container" className="checkout-container">

      {/* ORDER SUMMARY */}
      <div data-test-id="order-summary" className="card">
        <h2>Complete Payment</h2>

        <div className="row">
          <span>Amount</span>
          <span data-test-id="order-amount">₹{(order.amount / 100).toFixed(2)}</span>
        </div>

        <div className="row">
          <span>Order ID</span>
          <span data-test-id="order-id">{order.id}</span>
        </div>
      </div>

      {/* PAYMENT METHODS */}
      <div data-test-id="payment-methods" className="methods">
        <button data-test-id="method-upi" onClick={() => setMethod("upi")}>UPI</button>
        <button data-test-id="method-card" onClick={() => setMethod("card")}>Card</button>
      </div>

      {/* UPI FORM */}
      {method === "upi" && (
        <form data-test-id="upi-form" className="card"
          onSubmit={e => {
            e.preventDefault();
            pay({ order_id: order.id, method: "upi", vpa: e.target.vpa.value });
          }}
        >
          <input data-test-id="vpa-input" name="vpa" placeholder="username@bank" />
          <PaymentButton label={`Pay ₹${order.amount / 100}`} />
        </form>
      )}

      {/* CARD FORM */}
      {method === "card" && (
        <form data-test-id="card-form" className="card"
          onSubmit={e => {
            e.preventDefault();
            pay({ order_id: order.id, method: "card", card_number: e.target.card.value });
          }}
        >
          <input data-test-id="card-number-input" name="card" placeholder="Card Number" />
          <input data-test-id="expiry-input" placeholder="MM/YY" />
          <input data-test-id="cvv-input" placeholder="CVV" />
          <input data-test-id="cardholder-name-input" placeholder="Name on Card" />
          <PaymentButton label={`Pay ₹${order.amount / 100}`} />
        </form>
      )}

      {processing && (
        <div data-test-id="processing-state" className="state">
          <span data-test-id="processing-message">Processing payment...</span>
        </div>
      )}

      {success && (
        <div data-test-id="success-state" className="state success">
          <h2>Payment Successful!</h2>
          <span data-test-id="payment-id">{paymentId}</span>
          <span data-test-id="success-message">Payment processed successfully</span>
        </div>
      )}

      {error && (
        <div data-test-id="error-state" className="state error">
          <h2>Payment Failed</h2>
          <span data-test-id="error-message">Payment could not be processed</span>
          <button data-test-id="retry-button" onClick={() => window.location.reload()}>
            Try Again
          </button>
        </div>
      )}
    </div>
  );
}
