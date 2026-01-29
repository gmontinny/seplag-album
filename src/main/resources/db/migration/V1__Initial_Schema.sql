CREATE TABLE artista (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL -- 'CANTOR' ou 'BANDA'
);

CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    capa_url TEXT
);

CREATE TABLE artista_album (
    artista_id BIGINT REFERENCES artista(id),
    album_id BIGINT REFERENCES album(id),
    PRIMARY KEY (artista_id, album_id)
);

CREATE TABLE regional (
    internal_id BIGSERIAL PRIMARY KEY,
    id INTEGER NOT NULL,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- População inicial de artistas
INSERT INTO artista (nome, tipo) VALUES ('Serj Tankian', 'CANTOR');
INSERT INTO artista (nome, tipo) VALUES ('Mike Shinoda', 'CANTOR');
INSERT INTO artista (nome, tipo) VALUES ('Michel Teló', 'CANTOR');
INSERT INTO artista (nome, tipo) VALUES ('Guns N’ Roses', 'BANDA');

-- População inicial de álbuns
INSERT INTO album (titulo) VALUES ('Harakiri');
INSERT INTO album (titulo) VALUES ('Black Blooms');
INSERT INTO album (titulo) VALUES ('The Rough Dog');
INSERT INTO album (titulo) VALUES ('The Rising Tied');
INSERT INTO album (titulo) VALUES ('Post Traumatic');
INSERT INTO album (titulo) VALUES ('Post Traumatic EP');
INSERT INTO album (titulo) VALUES ('Where’d You Go');
INSERT INTO album (titulo) VALUES ('Bem Sertanejo');
INSERT INTO album (titulo) VALUES ('Bem Sertanejo - O Show (Ao Vivo)');
INSERT INTO album (titulo) VALUES ('Bem Sertanejo - (1ª Temporada) - EP');
INSERT INTO album (titulo) VALUES ('Use Your Illusion I');
INSERT INTO album (titulo) VALUES ('Use Your Illusion II');
INSERT INTO album (titulo) VALUES ('Greatest Hits');

-- Relacionamentos
INSERT INTO artista_album (artista_id, album_id) VALUES (1, 1), (1, 2), (1, 3);
INSERT INTO artista_album (artista_id, album_id) VALUES (2, 4), (2, 5), (2, 6), (2, 7);
INSERT INTO artista_album (artista_id, album_id) VALUES (3, 8), (3, 9), (3, 10);
INSERT INTO artista_album (artista_id, album_id) VALUES (4, 11), (4, 12), (4, 13);

-- Usuário padrão (senha: 'password')
-- Username: admin, Senha: password
INSERT INTO usuario (username, password) VALUES ('admin', '$2a$10$QBKjpDONOidW75jVancMX.E/19OUUFbZY2RAzpZWRhp0/eRRf/y8W');
