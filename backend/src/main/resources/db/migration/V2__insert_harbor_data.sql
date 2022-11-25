create table harbor
(
    id         integer  not null,
    coordinate geometry not null,
    name       varchar     not null,
    status     varchar     not null,
    country_id integer  not null
);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (1, ST_GeomFromText('POINT(30.8912 121.7424)'), 'Shanghai', 'AVAILABLE', 1);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (2, ST_GeomFromText('POINT(30.8912 121.7424)'), 'Tokio', 'AVAILABLE', 2);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (3, 'POINT(18.9601 72.8502)', 'Mumbai', 'AVAILABLE', 3);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (4, ST_GeomFromText('POINT(51.5000 0.0500)'), 'London', 'AVAILABLE', 4);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (5, ST_GeomFromText('POINT(38.7369 -9.1426)'), 'Lisbon', 'AVAILABLE', 5);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (6, ST_GeomFromText('POINT(40.6400 74.0500)'), 'New York', 'AVAILABLE', 6);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (7, ST_GeomFromText('POINT(37.7749 122.4194)'), 'San Francisco', 'AVAILABLE', 6);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (8, ST_GeomFromText('POINT(23.9660 23.9660)'), 'Sao Paulo', 'AVAILABLE', 7);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (9, ST_GeomFromText('POINT(33.9043 18.4301)'), 'Kapstadt', 'AVAILABLE', 8);

INSERT INTO harbor (id, coordinate, name, status, country_id)
VALUES (10, ST_GeomFromText('POINT(6.4478 3.3858)'), 'Lagos', 'AVAILABLE', 9);