-- Dev/test login: POST or PUT /auth/login with {"username":"demo","password":"demo"}
INSERT INTO users (username, password, role)
VALUES ('demo', 'demo', 'USER')
ON CONFLICT (username) DO NOTHING;
