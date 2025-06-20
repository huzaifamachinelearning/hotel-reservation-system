-- Inserting Economy Room
INSERT INTO room_type (
    room_type_id, type_name, price_per_night, max_occupancy,
    bed_type, has_ac, has_wifi, has_tv
) VALUES (
    1, 'economy', 2500.00, 2, 'double', 'n', 'y', 'y'
);

-- Inserting Standard Room
INSERT INTO room_type (
    room_type_id, type_name, price_per_night, max_occupancy,
    bed_type, has_ac, has_wifi, has_tv
) VALUES (
    2, 'standard', 4000.00, 3, 'queen', 'y', 'y', 'y'
);

-- Inserting Deluxe Room
INSERT INTO room_type (
    room_type_id, type_name, price_per_night, max_occupancy,
    bed_type, has_ac, has_wifi, has_tv
) VALUES (
    3, 'deluxe', 6000.00, 4, 'king', 'y', 'y', 'y'
);
commit;
