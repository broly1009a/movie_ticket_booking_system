import React from "react";
import DashboardLayout from "../DashboardLayout";
import { FiUsers, FiFilm, FiDollarSign, FiVideo } from "react-icons/fi";

// Fake data
const stats = [
  {
    label: "Tổng vé đã bán",
    value: 1523,
    icon: <FiUsers className="text-3xl text-blue-500" />,
    color: "bg-blue-100"
  },
  {
    label: "Tổng doanh thu",
    value: "₫ 320,000,000",
    icon: <FiDollarSign className="text-3xl text-green-500" />,
    color: "bg-green-100"
  },
  {
    label: "Phim đang chiếu",
    value: 12,
    icon: <FiFilm className="text-3xl text-purple-500" />,
    color: "bg-purple-100"
  },
  {
    label: "Phòng chiếu",
    value: 5,
    icon: <FiVideo className="text-3xl text-yellow-500" />,
    color: "bg-yellow-100"
  }
];

const revenueData = [
  { month: "1", value: 20 },
  { month: "2", value: 35 },
  { month: "3", value: 50 },
  { month: "4", value: 40 },
  { month: "5", value: 60 },
  { month: "6", value: 80 },
  { month: "7", value: 90 },
  { month: "8", value: 70 },
  { month: "9", value: 100 },
  { month: "10", value: 110 },
  { month: "11", value: 95 },
  { month: "12", value: 120 }
];

const topMovies = [
  { name: "The Great Movie", tickets: 320 },
  { name: "Comedy Night", tickets: 280 },
  { name: "Action Hero", tickets: 250 },
  { name: "Romantic Love", tickets: 210 },
  { name: "Horror Night", tickets: 180 }
];

const Dashboard = () => {
  // Tính max cho biểu đồ
  const maxRevenue = Math.max(...revenueData.map(d => d.value));

  return (
    <DashboardLayout>
      <div className="p-8">
        <h1 className="text-2xl font-bold mb-6">Báo cáo tổng quan hệ thống đặt vé phim</h1>
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {stats.map((stat, idx) => (
            <div key={idx} className={`rounded-xl p-6 flex items-center gap-4 shadow ${stat.color}`}>
              <div>{stat.icon}</div>
              <div>
                <div className="text-2xl font-bold">{stat.value}</div>
                <div className="text-gray-600">{stat.label}</div>
              </div>
            </div>
          ))}
        </div>
        {/* Revenue Chart */}
        <div className="bg-white rounded-xl shadow p-6 mb-8">
          <h2 className="text-xl font-bold mb-4">Doanh thu theo tháng (triệu đồng)</h2>
          <div className="flex items-end h-48 space-x-2">
            {revenueData.map((d, idx) => (
              <div key={idx} className="flex flex-col items-center flex-1">
                <div
                  className="bg-blue-500 rounded-t"
                  style={{
                    height: `${(d.value / maxRevenue) * 100}%`,
                    width: "80%"
                  }}
                  title={`${d.value} triệu`}
                ></div>
                <span className="text-xs mt-2">{d.month}</span>
              </div>
            ))}
          </div>
        </div>
        {/* Top Movies */}
        <div className="bg-white rounded-xl shadow p-6 mb-8">
          <h2 className="text-xl font-bold mb-4">Top 5 phim bán chạy</h2>
          <table className="w-full text-left">
            <thead>
              <tr>
                <th className="py-2">#</th>
                <th className="py-2">Tên phim</th>
                <th className="py-2">Số vé đã bán</th>
              </tr>
            </thead>
            <tbody>
              {topMovies.map((movie, idx) => (
                <tr key={movie.name} className="border-t">
                  <td className="py-2">{idx + 1}</td>
                  <td className="py-2">{movie.name}</td>
                  <td className="py-2">{movie.tickets}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {/* More widgets can be added here */}
      </div>
    </DashboardLayout>
  );
};

export default Dashboard;