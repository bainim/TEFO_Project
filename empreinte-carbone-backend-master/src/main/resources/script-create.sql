BEGIN;
INSERT INTO public.transit(id, name, co2) VALUES (1, 'subway', 2.5);
INSERT INTO public.transit(id, name, co2) VALUES (2, 'rer', 4.1);
INSERT INTO public.transit(id, name, co2) VALUES (3, 'tramway', 2.2);
INSERT INTO public.transit(id, name, co2) VALUES (4, 'bus', 103);
INSERT INTO public.transit(id, name, co2) VALUES (5, 'car-diesel', 193);
INSERT INTO public.transit(id, name, co2) VALUES (6, 'car-electric', 19.8);
INSERT INTO public.transit(id, name, co2) VALUES (7, 'car-gasoline', 193);
INSERT INTO public.transit(id, name, co2) VALUES (8, 'train', 24.8);
INSERT INTO public.transit(id, name, co2) VALUES (9, 'bike', 0);
INSERT INTO public.transit(id, name, co2) VALUES (10, 'bike-electric', 2);
INSERT INTO public.transit(id, name, co2) VALUES (11, 'feet', 0);
COMMIT;
