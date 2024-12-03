CREATE DATABASE eventojava;

USE eventojava;

CREATE TABLE Pessoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE TipoNotificacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('Telefone', 'Email') NOT NULL
);

INSERT INTO TipoNotificacao (id, tipo) VALUES
(1, 'Telefone'),
(2, 'Email');


CREATE TABLE Organizador (
    id INT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    FOREIGN KEY (id) REFERENCES Pessoa(id)
);

CREATE TABLE Participante (
    id INT PRIMARY KEY,
    telefone VARCHAR(20),
    FOREIGN KEY (id) REFERENCES Pessoa(id)
);

CREATE TABLE Local (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    vagas INT NOT NULL
);

CREATE TABLE Evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idOrganizador INT NOT NULL,
    idLocal INT NOT NULL,
    data DATETIME NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    vagas INT NOT NULL,
    FOREIGN KEY (idOrganizador) REFERENCES Organizador(id),
    FOREIGN KEY (idLocal) REFERENCES Local(id)
);

CREATE TABLE EventoParticipante (
    idEvento INT NOT NULL,
    idParticipante INT NOT NULL,
    PRIMARY KEY (idEvento, idParticipante),
    FOREIGN KEY (idEvento) REFERENCES Evento(id),
    FOREIGN KEY (idParticipante) REFERENCES Participante(id)
);

CREATE TABLE Notificacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idTipoNotificacao INT NOT NULL,
    idParticipante INT NOT NULL,
    idEvento INT NOT NULL,
    mensagem VARCHAR(255) NOT NULL,
    FOREIGN KEY (idEvento) REFERENCES Evento(id),
    FOREIGN KEY (idParticipante) REFERENCES Participante(id),
    FOREIGN KEY (idTipoNotificacao) REFERENCES TipoNotificacao(id)
);