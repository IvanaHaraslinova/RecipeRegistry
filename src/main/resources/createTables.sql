CREATE TABLE recipeCategories(
id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
title VARCHAR(70) UNIQUE);



CREATE TABLE recipes (
id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
title VARCHAR(70) UNIQUE,
instructions VARCHAR(4000),
note VARCHAR(200),
category BIGINT REFERENCES recipeCategories(id),
time INT);

CREATE TABLE ingrediences(
id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
title VARCHAR(70),
amount DOUBLE,
unit VARCHAR(15),
recipe BIGINT REFERENCES recipes(id) ON DELETE CASCADE);
