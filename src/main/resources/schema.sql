DROP TABLE IF EXISTS comments  CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users  CASCADE;

CREATE TABLE IF NOT EXISTS users (
    user_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    CONSTRAINT uc_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description varchar(200) NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT fk_requests_user_id FOREIGN KEY(user_id) REFERENCES users (user_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS items (
    item_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL,
    description varchar(200) NOT NULL,
    available boolean NOT NULL,
    user_id bigint NOT NULL,
    request_id bigint,
    CONSTRAINT fk_items_user_id FOREIGN KEY(user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_items_request_id FOREIGN KEY(request_id) REFERENCES requests (request_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS bookings (
    booking_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date timestamp without time zone NOT NULL,
    end_date timestamp without time zone NOT NULL,
    item_id bigint NOT NULL,
    user_id bigint NOT NULL,
    status varchar(20) NOT NULL,
    CONSTRAINT fk_bookings_item_id FOREIGN KEY(item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_user_id FOREIGN KEY(user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text varchar(500) NOT NULL,
    item_id bigint NOT NULL,
    user_id bigint NOT NULL,
    created timestamp without time zone NOT NULL,
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fc_comments_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);


