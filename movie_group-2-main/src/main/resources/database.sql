CREATE TABLE role (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE account (
    account_id SERIAL PRIMARY KEY,
    address VARCHAR(255),
    birthday DATE,
    email VARCHAR(255),
    name VARCHAR(255),
    gender VARCHAR(10),
    identity_card VARCHAR(50),
    image VARCHAR(255),
    password VARCHAR(255),
    phone_number VARCHAR(50),
    register_date DATE,
    status BOOLEAN,
    username VARCHAR(255) UNIQUE NOT NULL,
    role_id INTEGER REFERENCES role(role_id)
);

CREATE TABLE member (
    member_id SERIAL PRIMARY KEY,
    score INTEGER,
    account_id INTEGER UNIQUE REFERENCES account(account_id)
);

CREATE TABLE employee (
    employee_id SERIAL PRIMARY KEY,
    account_id INTEGER UNIQUE REFERENCES account(account_id)
);

CREATE TABLE invoice (
    invoice_id SERIAL PRIMARY KEY,
    account_id INTEGER REFERENCES account(account_id),
    add_score INTEGER,
    booking_date TIMESTAMP,
    movie_name VARCHAR(255),
    schedule_show VARCHAR(255),
    schedule_show_time VARCHAR(255),
    status BOOLEAN,
    total_money INTEGER,
    use_score INTEGER,
    seat VARCHAR(255)
);

CREATE TABLE type (
    type_id SERIAL PRIMARY KEY,
    type_name VARCHAR(255)
);

CREATE TABLE movie (
    movie_id VARCHAR(10) PRIMARY KEY,
    actor VARCHAR(255),
    cinema_room_id INTEGER,
    content VARCHAR(1000),
    director VARCHAR(255),
    duration INTEGER,
    from_date DATE,
    movie_production_company VARCHAR(255),
    to_date DATE,
    version VARCHAR(255),
    movie_name_english VARCHAR(255),
    movie_name_vn VARCHAR(255),
    large_image VARCHAR(255),
    small_image VARCHAR(255)
);

CREATE TABLE schedule (
    schedule_id SERIAL PRIMARY KEY,
    schedule_time VARCHAR(255)
);

CREATE TABLE show_dates (
    show_date_id SERIAL PRIMARY KEY,
    show_date DATE,
    date_name VARCHAR(255)
);

CREATE TABLE movie_type (
    movie_id VARCHAR(10) REFERENCES movie(movie_id),
    type_id INTEGER REFERENCES type(type_id),
    PRIMARY KEY (movie_id, type_id)
);

CREATE TABLE movie_schedule (
    movie_id VARCHAR(10) REFERENCES movie(movie_id),
    schedule_id INTEGER REFERENCES schedule(schedule_id),
    PRIMARY KEY (movie_id, schedule_id)
);

CREATE TABLE movie_date (
    movie_id VARCHAR(10) REFERENCES movie(movie_id),
    show_date_id INTEGER REFERENCES show_dates(show_date_id),
    PRIMARY KEY (movie_id, show_date_id)
);
CREATE TABLE cinema_room (
    cinema_room_id SERIAL PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL,
    description TEXT
);
CREATE TABLE seat (
    seat_id SERIAL PRIMARY KEY,
    cinema_room_id INTEGER REFERENCES cinema_room(cinema_room_id),
    row_label VARCHAR(5),      -- Ví dụ: 'A', 'B', 'C'
    seat_number INTEGER,       -- Ví dụ: 1, 2, 3
    seat_code VARCHAR(10),     -- Ví dụ: 'A1', 'B5'
    seat_type VARCHAR(20),     -- Ví dụ: 'VIP', 'NORMAL'
    status BOOLEAN DEFAULT true,
    UNIQUE(cinema_room_id, seat_code)
);
-- Role
INSERT INTO role (role_name) VALUES ('GUEST'), ('MEMBER'), ('EMPLOYEE'), ('ADMIN');

-- Account
INSERT INTO account (address, birthday, email, name, gender, identity_card, image, password, phone_number, register_date, status, username, role_id)
VALUES
('123 Main St', '2000-01-01', 'user1@example.com', 'User One', 'MALE', '123456789', NULL, 'password1', '0123456789', '2024-01-01', true, 'user1', 2),
('456 Second St', '1999-05-15', 'user2@example.com', 'User Two', 'FEMALE', '987654321', NULL, 'password2', '0987654321', '2024-01-02', true, 'user2', 3);

-- Member
INSERT INTO member (score, account_id) VALUES (100, 1);

-- Employee
INSERT INTO employee (account_id) VALUES (2);

-- Movie
INSERT INTO movie (movie_id, actor, cinema_room_id, content, director, duration, from_date, movie_production_company, to_date, version, movie_name_english, movie_name_vn, large_image, small_image)
VALUES
('M001', 'Actor A', 1, 'A great movie', 'Director A', 120, '2024-07-01', 'Company A', '2024-07-31', 'v1', 'The Great Movie', 'Phim Tuyệt Vời', NULL, NULL),
('M002', 'Actor B', 2, 'Another movie', 'Director B', 90, '2024-07-10', 'Company B', '2024-08-10', 'v1', 'Another Movie', 'Phim Khác', NULL, NULL);

-- Type
INSERT INTO type (type_id, type_name) VALUES (1, 'Action'), (2, 'Comedy');

-- Schedule
INSERT INTO schedule (schedule_id, schedule_time) VALUES (1, '09:00'), (2, '14:00');

-- ShowDates
INSERT INTO show_dates (show_date_id, show_date, date_name) VALUES (1, '2024-07-01', 'Monday'), (2, '2024-07-02', 'Tuesday');

-- MovieType
INSERT INTO movie_type (movie_id, type_id) VALUES ('M001', 1), ('M002', 2);

-- MovieSchedule
INSERT INTO movie_schedule (movie_id, schedule_id) VALUES ('M001', 1), ('M002', 2);

-- MovieDate
INSERT INTO movie_date (movie_id, show_date_id) VALUES ('M001', 1), ('M002', 2);

-- Invoice
INSERT INTO invoice (account_id, add_score, booking_date, movie_name, schedule_show, schedule_show_time, status, total_money, use_score, seat)
VALUES
(1, 10, '2024-07-01 08:00:00', 'The Great Movie', '09:00', '09:00', true, 100000, 0, 'A1'),
(2, 5, '2024-07-02 13:00:00', 'Another Movie', '14:00', '14:00', true, 90000, 0, 'B2');

-- Cinema Room
INSERT INTO cinema_room (room_name, capacity, description)
VALUES 
  ('Phòng 1', 120, 'Phòng chiếu tiêu chuẩn với âm thanh Dolby Digital'),
  ('Phòng 2 - IMAX', 200, 'Phòng IMAX màn hình lớn, âm thanh vòm cao cấp'),
  ('Phòng 3 - 3D', 150, 'Phòng chiếu phim 3D, hỗ trợ kính phân cực');

-- Seat
INSERT INTO seat (cinema_room_id, row_label, seat_number, seat_code, seat_type)
VALUES
(1, 'A', 1, 'A1', 'NORMAL'),
(1, 'A', 2, 'A2', 'NORMAL'),
(1, 'B', 1, 'B1', 'VIP'),
(1, 'B', 2, 'B2', 'VIP'),
(1, 'C', 1, 'C1', 'NORMAL');