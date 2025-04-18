CREATE OR REPLACE FUNCTION get_best_sales(start_date TIMESTAMP, end_date TIMESTAMP, limit_count INT)
RETURNS TABLE(dish_name VARCHAR, quantity_sold INT, total_amount NUMERIC) AS $$
BEGIN
RETURN QUERY
SELECT d.name AS dish_name,
       SUM(do.quantity)::INT AS quantity_sold,
        SUM(d.unit_price * do.quantity) AS total_amount
FROM "Order" o
         JOIN Dish_Order do ON o.order_id = do.order_id
         JOIN Dish d ON do.dish_id = d.dish_id
WHERE o.status = 'FINISHED'::status_type
      AND o.created_at BETWEEN start_date AND end_date
GROUP BY d.dish_id, d.name
ORDER BY quantity_sold DESC
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_processing_time(
    dish_id INT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    time_unit VARCHAR,
    calc_type VARCHAR
)
RETURNS NUMERIC AS $$
DECLARE
result NUMERIC;
    seconds_in_minute NUMERIC := 60;
    seconds_in_hour NUMERIC := 3600;
BEGIN
    IF calc_type = 'minimum' THEN
SELECT MIN(EXTRACT(EPOCH FROM (dos_finished.changed_at - dos_in_progress.changed_at))
           INTO result
           FROM Dish_Order do
           JOIN Dish_Order_Status dos_in_progress ON do.dish_order_id = dos_in_progress.dish_order_id
               AND dos_in_progress.status = 'IN_PROGRESS'::status_type
           JOIN Dish_Order_Status dos_finished ON do.dish_order_id = dos_finished.dish_order_id
               AND dos_finished.status = 'FINISHED'::status_type
           WHERE do.dish_id = dish_id
               AND dos_in_progress.changed_at BETWEEN start_date AND end_date;

ELSIF calc_type = 'maximum' THEN
SELECT MAX(EXTRACT(EPOCH FROM (dos_finished.changed_at - dos_in_progress.changed_at))
           INTO result
           FROM Dish_Order do
           JOIN Dish_Order_Status dos_in_progress ON do.dish_order_id = dos_in_progress.dish_order_id
               AND dos_in_progress.status = 'IN_PROGRESS'::status_type
           JOIN Dish_Order_Status dos_finished ON do.dish_order_id = dos_finished.dish_order_id
               AND dos_finished.status = 'FINISHED'::status_type
           WHERE do.dish_id = dish_id
               AND dos_in_progress.changed_at BETWEEN start_date AND end_date;

ELSE -- average
SELECT AVG(EXTRACT(EPOCH FROM (dos_finished.changed_at - dos_in_progress.changed_at))
           INTO result
           FROM Dish_Order do
           JOIN Dish_Order_Status dos_in_progress ON do.dish_order_id = dos_in_progress.dish_order_id
               AND dos_in_progress.status = 'IN_PROGRESS'::status_type
           JOIN Dish_Order_Status dos_finished ON do.dish_order_id = dos_finished.dish_order_id
               AND dos_finished.status = 'FINISHED'::status_type
           WHERE do.dish_id = dish_id
               AND dos_in_progress.changed_at BETWEEN start_date AND end_date;
END IF;

    IF time_unit = 'minutes' THEN
        result := result / seconds_in_minute;
    ELSIF time_unit = 'hours' THEN
        result := result / seconds_in_hour;
END IF;

RETURN result;
END;
$$ LANGUAGE plpgsql;