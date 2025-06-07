import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import MovieCard from '../Components/MovieCard';
import SubBanner from '../Components/SubBanner';
import NoData from '../Components/Search/noData';
import Subscribe from '../Components/Subscribe';
import Pagination from '../Components/Pagination';

const SearchPage = ({currentPage, setCurrentPage, setWatchList, watchList }) => {
  const { query } = useParams();
  const [searchResults, setSearchResults] = useState([]);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    const signal = controller.signal;
    fetch('http://localhost:8080/api/movies/search', { signal })
      .then((res) => res.json())
      .then((data) => {
        const movies = Array.isArray(data.Search) ? data.Search : [];
        const filtered = movies.filter((movie) =>
          movie.Title?.toLowerCase().includes(query.toLowerCase())
        );
        setTotalPages(Math.ceil(filtered.length / 8));
        setSearchResults(
          filtered.slice((currentPage - 1) * 8, currentPage * 8)
        );
      });

    return () => {
      controller.abort();
    };
  }, [query, currentPage]);

  return (
    <>
      <SubBanner title={'Search Results'} pathName={'Search'} />
      <section className='results-sec'>
        <div className='container'>
          <div className='section-title'>
            <h5 className='sub-title'>ONLINE STREAMING</h5>
            <h2 className='title'>{query}'s Related Results</h2>
          </div>
          <div className='row movies-grid'>
            {searchResults.length ? (
              searchResults.map((movie) => (
                <MovieCard movie={movie} key={movie.imdbID} setWatchList={setWatchList} watchList={watchList} />
              ))
            ) : (
              <NoData />
            )}
          </div>
          {
            totalPages > 1 &&
            (
              <Pagination
                totalPages={totalPages}
                currentPage={currentPage}
                setCurrentPage={setCurrentPage}
              />
            )
          }
        </div>
      </section>
      <Subscribe />
    </>
  );
};

export default SearchPage;