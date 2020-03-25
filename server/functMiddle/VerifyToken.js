const jwt = require('jsonwebtoken');

//funcion del middleware para comprobar la sesion
module.exports = function verifyToken (req, res, next) {

    const token = req.header('auth-token');
    if (!token) return res.status(401).send('Access Denied');

    try {
        const verified = jwt.verify(token, "dfsdkhnsdmvnkdjvn"/*process.env.nomvariabletoken*/);
        req.username = verified;
        next();

    } catch(error) {
        res.status(400).send('Invalid Token');
    }
}