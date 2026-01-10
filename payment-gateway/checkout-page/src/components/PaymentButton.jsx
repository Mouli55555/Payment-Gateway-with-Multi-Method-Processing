export default function PaymentButton({ label }) {
  return (
    <button className="pay-btn" data-test-id="pay-button" type="submit">
      {label}
    </button>
  );
}
