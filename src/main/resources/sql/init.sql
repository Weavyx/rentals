CREATE TABLE IF NOT EXISTS users (
                                     id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS rentals (
                                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name        VARCHAR(255) NOT NULL,
    surface     DECIMAL(10, 2) NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    picture     VARCHAR(500),
    description TEXT NOT NULL,
    owner_id    BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rental_owner FOREIGN KEY (owner_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS messages (
                                        id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        rental_id  BIGINT NOT NULL,
                                        user_id    BIGINT NOT NULL,
                                        message    TEXT NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        CONSTRAINT fk_message_rental FOREIGN KEY (rental_id) REFERENCES rentals(id),
    CONSTRAINT fk_message_user   FOREIGN KEY (user_id)   REFERENCES users(id)
    );