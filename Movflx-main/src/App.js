import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Footer from './Components/Footer';
import Navbar from './Components/Navbar';
import Home from './Pages/Home';
import NotFound from './Pages/NotFound';
import SearchPage from './Pages/SearchPage';
import SingleMovie from './Pages/SingleMovie';
import Search from './Components/Search';
import Favourites from './Pages/Favourites';
import MovieDashboard from './Pages/Admin/Movie/MovieDashboard';
import CinemaRoomDashboard from './Pages/Admin/CinemaRoom/CinemaRoomDashboard';
import TypeDashboard from './Pages/Admin/Type/TypeDashboard';
import Dashboard from './Pages/Admin/Dashboard/Dashboard';
import Login from './Pages/Login';
import Register from './Pages/Register';
import Personal from './Pages/Personal';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import axios from 'axios';
import Movies from './Pages/Movies';
import Showtimes from './Pages/Showtimes';
import SeatMap from './Pages/SeatMap';
import ConfirmBooking from './Pages/ConfirmBooking';
import BookingHistory from './Pages/BookingHistory';
import RequireRole from './Components/RequireRole';

function App() {
  const [showSearch, setShowSearch] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [watchList, setWatchList] = useState(JSON.parse(localStorage.getItem('watchList')) || []);
  const [user, setUser] = useState(JSON.parse(localStorage.getItem('user')) || null);

  useEffect(() => {
    localStorage.setItem('user', JSON.stringify(user));
  }, [user]);

  const handleLogout = async () => {
    try {
      await axios.post('/api/logout');
      toast.success('Logout successful');
    } catch (err) {
      console.error('Logout failed:', err);
    }
    setUser(null);
    localStorage.removeItem('user');
  };

  return (
    <div className="App">
      <BrowserRouter>
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
          toastStyle={{ backgroundColor: '#333', color: '#fff' }}
        />
        <Routes>
          <Route
            path="/admin/movies"
            element={
              <RequireRole user={user} roles={["ADMIN", "EMPLOYEE"]}>
                <MovieDashboard />
              </RequireRole>
            }
          />
          <Route
            path="/admin/types"
            element={
              <RequireRole user={user} roles={["ADMIN", "EMPLOYEE"]}>
                <TypeDashboard />
              </RequireRole>
            }
          />
          <Route
            path="/admin/cinema-rooms"
            element={
              <RequireRole user={user} roles={["ADMIN", "EMPLOYEE"]}>
                <CinemaRoomDashboard />
              </RequireRole>
            }
          />
          <Route
            path="/admin/dashboard"
            element={
              <RequireRole user={user} roles={["ADMIN", "EMPLOYEE"]}>
                <Dashboard />
              </RequireRole>
            }
          />
          <Route
            path="/*"
            element={
              <>
                <Navbar setShowSearch={setShowSearch} watchList={watchList} user={user} onLogout={handleLogout} />
                <Search showSearch={showSearch} setShowSearch={setShowSearch} setCurrentPage={setCurrentPage} />
                <Routes>
                  <Route path="/" element={<Home setWatchList={setWatchList} watchList={watchList} />} />
                  <Route
                    path="search/:query"
                    element={
                      <SearchPage
                        setCurrentPage={setCurrentPage}
                        currentPage={currentPage}
                        setWatchList={setWatchList}
                        watchList={watchList}
                      />
                    }
                  />
                  <Route path="movies" element={<Movies setWatchList={setWatchList} watchList={watchList} />} />
                  <Route path="movie/:id" element={<SingleMovie />} />
                  <Route path="favourites" element={<Favourites watchList={watchList} setWatchList={setWatchList} />} />
                  <Route path="showtimes/:movieId" element={<Showtimes />} />
                  <Route path="seat-map/:movieId/:showDateId/:scheduleId" element={<SeatMap />} />
                  <Route
                    path="/confirm-booking/:accountId"
                    element={
                      <RequireRole user={user} authOnly>
                        <ConfirmBooking />
                      </RequireRole>
                    }
                  />
                  <Route
                    path="/booking-history"
                    element={
                      <RequireRole user={user} authOnly>
                        <BookingHistory />
                      </RequireRole>
                    }
                  />
                  <Route
                    path="/login"
                    element={
                      <RequireRole user={user} guestOnly>
                        <Login setUser={setUser} />
                      </RequireRole>
                    }
                  />
                  <Route
                    path="/register"
                    element={
                      <RequireRole user={user} guestOnly>
                        <Register setUser={setUser} />
                      </RequireRole>
                    }
                  />
                  <Route
                    path="/personal"
                    element={
                      <RequireRole user={user} authOnly>
                        <Personal user={user} />
                      </RequireRole>
                    }
                  />
                  <Route path="*" element={<NotFound />} />
                </Routes>
                <Footer />
              </>
            }
          />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;