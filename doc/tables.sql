DROP TABLE IF EXISTS role_position;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_position;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS position;
DROP TABLE IF EXISTS departamento;
DROP TABLE IF EXISTS log;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS props;

-- Table: props
-- DROP TABLE props;

CREATE TABLE props (
  chave character varying(80) NOT NULL, 
  valor character varying(100) NOT NULL)
WITH (OIDS=FALSE);


-- Table: usuario
-- DROP TABLE usuario;

CREATE TABLE usuario (
  chave serial NOT NULL,
  cod character varying(20) NOT NULL,
  usuario character varying(80),
  ativo character varying(5),
  CONSTRAINT usuario_pkey PRIMARY KEY (chave)
)
WITH (OIDS=FALSE);
ALTER TABLE usuario OWNER TO "CSP_JOB";
GRANT ALL ON TABLE usuario TO "CSP_JOB";
GRANT ALL ON TABLE usuario TO public;

-- Table: log
-- DROP TABLE log;

CREATE TABLE log (
  cod bigint,
  usuario character varying(20),
  ini timestamp with time zone,
  fim timestamp with time zone
)
WITH (OIDS=FALSE);
ALTER TABLE log OWNER TO "CSP_JOB";
GRANT ALL ON TABLE log TO "CSP_JOB";
GRANT ALL ON TABLE log TO public;

-- Table: departamento
-- DROP TABLE departamento;

CREATE TABLE departamento (
  cod serial NOT NULL,
  descricao character varying(40),
  abrev character varying(6),
  CONSTRAINT departamento_pkey PRIMARY KEY (cod)
)
WITH (OIDS=FALSE);

ALTER TABLE departamento OWNER TO "CSP_JOB";

-- Table: "position"
-- DROP TABLE "position";
CREATE TABLE "position" (
  cod serial NOT NULL,
  descricao character varying(40),
  cod_departamento integer,
  CONSTRAINT position_pkey PRIMARY KEY (cod),
  CONSTRAINT position_cod_departamento_fkey FOREIGN KEY (cod_departamento)
      REFERENCES departamento (cod) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=FALSE);
ALTER TABLE "position" OWNER TO "CSP_JOB";

-- Table: role
-- DROP TABLE role;
CREATE TABLE role (
  cod serial NOT NULL,
  descricao character varying(40),
  CONSTRAINT role_pkey PRIMARY KEY (cod)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE role
  OWNER TO "CSP_JOB";

-- Table: role_position
-- DROP TABLE role_position;

CREATE TABLE role_position (
  cod_role integer NOT NULL,
  cod_position integer NOT NULL,
  CONSTRAINT role_position_pkey PRIMARY KEY (cod_role , cod_position ),
  CONSTRAINT role_position_cod_position_fkey FOREIGN KEY (cod_position)
      REFERENCES "position" (cod) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT role_position_cod_role_fkey FOREIGN KEY (cod_role)
      REFERENCES role (cod) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE role_position
  OWNER TO "CSP_JOB";

-- Table: user_position
-- DROP TABLE user_position;

CREATE TABLE user_position (
  cod serial NOT NULL,
  cod_usuario integer NOT NULL,
  cod_position integer NOT NULL,
  valid_from timestamp without time zone,
  valid_to timestamp without time zone,
  ticket integer,
  CONSTRAINT user_position_pkey PRIMARY KEY (cod),
  CONSTRAINT user_position_cod_position_fkey FOREIGN KEY (cod_position)
      REFERENCES "position" (cod) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT user_position_cod_usuario_fkey FOREIGN KEY (cod_usuario)
      REFERENCES usuario (chave) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_position
  OWNER TO "CSP_JOB";


-- Table: user_roles
-- DROP TABLE user_roles;
CREATE TABLE user_roles (
  cod_user_position integer NOT NULL,
  cod_role integer NOT NULL,
  CONSTRAINT user_roles_pkey PRIMARY KEY (cod_user_position , cod_role ),
  CONSTRAINT user_roles_cod_role_fkey FOREIGN KEY (cod_role)
      REFERENCES role (cod) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT user_roles_cod_user_position_fkey FOREIGN KEY (cod_user_position)
      REFERENCES user_position (cod) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_roles
  OWNER TO "CSP_JOB";
