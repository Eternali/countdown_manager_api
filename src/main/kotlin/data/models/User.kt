package org.example.CountdownManagerApi

/**
 * Signup / login flow
 *
 * 1. enter email + password
 * 2. POST email + hashedPassword
 * 3. check if user exists:
 *   - if found: check hash(hashedPassword + salt) == hashdPwS (hash stored in db)
 *   - else: generate salt and save new user
 * 4. respond with JWT
 */



class User(
    val email: String,
    hashedPassword: String
) {

    private var salt: String = generateSalt()
    private var hashedPwS: String

    init {
        this.hashedPwS = (salt + hashedPassword)
            .calculateSHA3(SHA3Parameter.SHA3_256)
            .toString()
    }

}