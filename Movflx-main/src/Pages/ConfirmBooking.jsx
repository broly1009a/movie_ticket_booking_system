import React, { useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

const ConfirmBooking = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { accountId } = useParams();

  // Nhận thông tin đặt vé từ location.state (truyền từ SeatMap)
  const bookingInfo = location.state || {};

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleConfirm = async () => {
    setLoading(true);
    try {
      const res = await fetch(
        `http://localhost:8080/api/booking/confirm?accountId=${accountId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(bookingInfo),
        }
      );
      if (res.ok) {
        setSuccess(true);
        setTimeout(() => navigate("/"), 2000);
      } else {
        alert("Đặt vé thất bại!");
      }
    } catch (err) {
      alert("Có lỗi xảy ra!");
    }
    setLoading(false);
  };

  if (!bookingInfo || !bookingInfo.movieName) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
        <div className="bg-gray-800 p-8 rounded-lg shadow-2xl">
          <h2 className="text-2xl mb-4">Không có thông tin đặt vé!</h2>
          <button
            className="bg-blue-600 px-6 py-2 rounded text-white"
            onClick={() => navigate(-1)}
          >
            Quay lại
          </button>
        </div>
      </div>
    );
  }

  return (
    <section className="results-sec">
      <div className="container">
        <div className="section-title">
          <h5 className="sub-title">Xác nhận đặt vé</h5>
          <h2 className="title">Thông tin đặt vé</h2>
        </div>
        <div className="min-h-screen bg-gray-900 p-8 flex justify-center items-center">
          <div className="max-w-lg w-full bg-gray-800 p-8 rounded-lg shadow-2xl">
            <div className="mb-6">
              <div className="flex justify-between mb-2 text-gray-300">
                <span>Tên phim:</span>
                <span className="font-bold text-white">{bookingInfo.movieName}</span>
              </div>
              <div className="flex justify-between mb-2 text-gray-300">
                <span>Suất chiếu:</span>
                <span className="font-bold text-white">{bookingInfo.scheduleShow}</span>
              </div>
              <div className="flex justify-between mb-2 text-gray-300">
                <span>Ngày đặt:</span>
                <span className="font-bold text-white">{bookingInfo.bookingDate}</span>
              </div>
              <div className="flex justify-between mb-2 text-gray-300">
                <span>Ghế:</span>
                <span className="font-bold text-white">{bookingInfo.seat}</span>
              </div>
              <div className="flex justify-between mb-2 text-gray-300">
                <span>Tổng tiền:</span>
                <span className="font-bold text-green-400">{bookingInfo.totalMoney?.toLocaleString("vi-VN")} VND</span>
              </div>
              <div className="flex justify-between mb-2 text-gray-300">
                <span>Dùng điểm:</span>
                <span className="font-bold text-white">{bookingInfo.useScore}</span>
              </div>
              <div className="flex justify-between mb-2 text-gray-300">
                <span>Điểm cộng:</span>
                <span className="font-bold text-white">{bookingInfo.addScore}</span>
              </div>
            </div>
            {success ? (
              <div className="text-green-400 text-center font-bold text-lg">Đặt vé thành công!</div>
            ) : (
              <button
                onClick={handleConfirm}
                disabled={loading}
                className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition duration-200"
              >
                {loading ? "Đang xác nhận..." : "Xác nhận đặt vé"}
              </button>
            )}
          </div>
        </div>
      </div>
    </section>
  );
};

export default ConfirmBooking;