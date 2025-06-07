import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiHome, FiFilm, FiPlusCircle, FiGrid, FiMenu } from "react-icons/fi";
const Sidebar = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const navigate = useNavigate();
  const menuItems = [
    { icon: FiHome, text: "Dashboard", path: "/admin/dashboard" },
    { icon: FiFilm, text: "Movies", path: "/admin/movies" },
    { icon: FiPlusCircle, text: "Cinema Rooms", path: "/admin/cinema-rooms" },
    { icon: FiGrid, text: "Categories", path: "/admin/types" }
  ];
  return (
    <div className={`${sidebarOpen ? 'w-64' : 'w-20'} bg-white shadow-lg transition-all duration-300`}>
      <div className="p-4">
        <button
          onClick={() => setSidebarOpen(!sidebarOpen)}
          className="p-2 hover:bg-gray-100 rounded-full"
        >
          <FiMenu size={24} />
        </button>
      </div>
      <nav className="mt-4">
        {menuItems.map((item, index) => (
          <button
            key={index}
            className="w-full p-4 flex items-center gap-4 hover:bg-gray-100"
            onClick={() => navigate(item.path)}
          >
            <item.icon size={20} />
            {sidebarOpen && <span>{item.text}</span>}
          </button>
        ))}
      </nav>
    </div>
  );
}
export default Sidebar;