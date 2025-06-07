import React, { useEffect, useState } from 'react';
import MovieCard from '../Components/MovieCard';
import NoData from '../Components/Search/noData';
import Subscribe from '../Components/Subscribe';
import Pagination from '../Components/Pagination';

const Movies = ({ setWatchList, watchList }) => {
    const [movies, setMovies] = useState([]);
    const [types, setTypes] = useState([]);
    const [selectedType, setSelectedType] = useState('All');
    const [searchTerm, setSearchTerm] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    useEffect(() => {
        fetch('http://localhost:8080/api/types')
            .then(res => res.json())
            .then(data => setTypes([{ typeId: 0, typeName: 'All' }, ...data]));
    }, []);

    useEffect(() => {
        const url =
            selectedType === 'All'
                ? `http://localhost:8080/api/movies/search?page=${currentPage}`
                : `http://localhost:8080/api/movies/search?type=${selectedType}&page=${currentPage}`;
        fetch(url)
            .then(res => res.json())
            .then(data => {
                if (data.Search) {
                    setMovies(data.Search);
                    setTotalPages(Math.ceil(Number(data.totalResults) / 10));
                } else if (Array.isArray(data)) {
                    setMovies(data.slice(0, 20));
                    setTotalPages(1);
                } else {
                    setMovies([]);
                    setTotalPages(1);
                }
            });
    }, [selectedType, currentPage]);

    // Reset về trang 1 khi đổi loại phim
    const handleTypeChange = (typeName) => {
        setSelectedType(typeName);
        setCurrentPage(1);
    };

    // Lọc phim theo searchTerm
    const filteredMovies = movies.filter(movie =>
        movie.Title?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <>
            <section className='results-sec'>
                <div className='container' style={{ display: 'flex', alignItems: 'flex-start' }}>
                    {/* Sidebar */}
                    <aside className="sidebar" style={{ width: 220, background: '#171d22', padding: 24, borderRadius: 12, marginRight: 32, marginTop: 118 }}>
                        <div style={{ marginBottom: 24 }}>
                            <input
                                type="text"
                                placeholder="Search..."
                                value={searchTerm}
                                onChange={e => setSearchTerm(e.target.value)}
                                style={{
                                    width: '100%',
                                    padding: '10px 14px',
                                    borderRadius: 6,
                                    border: '1.5px solid #e4d804',
                                    fontSize: 14,
                                    background: '#171d22',
                                    color: '#fff',
                                    outline: 'none'
                                }}
                            />
                        </div>
                        <h3 style={{ color: '#e4d804', marginBottom: 16 }}>Categories</h3>
                        <ul style={{ listStyle: 'none', padding: 0 }}>
                            {types.map(type => (
                                <li key={type.typeId} style={{ marginBottom: 12 }}>
                                    <button
                                        style={{
                                            background: selectedType === type.typeName ? '#e4d804' : 'transparent',
                                            color: selectedType === type.typeName ? '#171d22' : '#fff',
                                            border: 'none',
                                            padding: '10px 18px',
                                            borderRadius: 6,
                                            width: '100%',
                                            textAlign: 'left',
                                            fontWeight: 600,
                                            cursor: 'pointer'
                                        }}
                                        onClick={() => handleTypeChange(type.typeName)}
                                    >
                                        {type.typeName}
                                    </button>
                                </li>
                            ))}
                        </ul>
                    </aside>
                    {/* Movie List */}
                    <div style={{ flex: 1 }}>
                        <div className='section-title'>
                            <h5 className='sub-title'>ONLINE STREAMING</h5>
                            <h2 className='title'>All Movies</h2>
                        </div>
                        <div className='row movies-grid'>
                            {filteredMovies.length ? (
                                filteredMovies.map(movie => (
                                    <MovieCard
                                        movie={movie}
                                        key={movie.imdbID || movie.movieId}
                                        setWatchList={setWatchList}
                                        watchList={watchList}
                                    />
                                ))
                            ) : (
                                <NoData />
                            )}
                        </div>
                        {totalPages > 1 && (
                            <Pagination
                                totalPages={totalPages}
                                currentPage={currentPage}
                                setCurrentPage={setCurrentPage}
                            />
                        )}
                    </div>
                </div>
            </section>
            <Subscribe />
        </>
    );
};

export default Movies;