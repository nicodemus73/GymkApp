const jwt = require('jsonwebtoken');
const User = require('../bbdd/UserSchema');

//funcion del middleware para comprobar la sesion
module.exports = function verifyToken(req, res, next) {

    const token = req.header('Authorization');
    if (!token) return res.status(400).json({ "error": 'Access Denied, no token provided' }); //no token provided

    try {
        const verified = jwt.verify(token, "dfsdkhnsdmvnkdjvn"/*process.env.nomvariabletoken*/);

        User.findById(verified._id).exec(function (err, user) {
            if (err) res.status(400).json({ "error": err.message });
            else if (user == null)
                res.status(404).json({
                    "error": 'User ID provided in token does not exist.',
                    "ID": verified._id
                });
            else {
                console.log('You provived a token with user _id: ', verified._id);
                req.usernameId = verified;
                next();
            }
        })

    } catch (error) {
        res.status(400).json({ "error": 'Invalid Token' });
    }
}