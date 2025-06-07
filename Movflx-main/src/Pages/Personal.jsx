import React from 'react';
import { useNavigate } from 'react-router-dom';

function Personal({ user }) {
  const navigate = useNavigate();

  if (!user) {
    navigate('/login');
    return null;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-gray-900 to-gray-800 text-gray-100">
      <div className="bg-white bg-opacity-5 p-8 rounded-xl backdrop-blur-lg shadow-lg w-80">
        <h1 className="text-3xl mb-6">Welcome, {user.username}!</h1>
        <p>Name: <span>{user.name}</span></p>
        <p>Email: <span>{user.email}</span></p>
        <p>Phone Number: <span>{user.phoneNumber}</span></p>
        <p>Address: <span>{user.address}</span></p>
        <p>Birthday: <span>{user.birthday}</span></p>
        <p>Gender: <span>{user.gender}</span></p>
            {/* Xem lịch sử đặt vé */}
        <div className="mt-6">
            <button
            onClick={() => navigate('/booking-history')}
            className="mt-2 px-4 py-2 bg-[#e4d804] hover:bg-[#cfc200] text-black rounded-full font-semibold transition"
            >
            Xem lịch sử đặt vé
            </button>
       </div>
      </div>
  
    </div>
  );
}

export default Personal;