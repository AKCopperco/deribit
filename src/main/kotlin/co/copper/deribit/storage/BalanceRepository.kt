package co.copper.deribit.storage

import co.copper.deribit.model.UserBalance
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BalanceRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val rowMapper = UserBalanceRowMapper()

    fun getUserBalance(username: String, currency: String): UserBalance? {
        return jdbcTemplate.query(
            """
                SELECT user_name, currency, balance, reserved FROM "deribit.user_balance"
                WHERE user_name = :username AND currency = :currency
            """.trimIndent(),
            MapSqlParameterSource("username", username)
                .addValue("currency", currency),
            rowMapper
        ).firstOrNull()
    }

    fun update(balance: UserBalance) {
        jdbcTemplate.update(
            """
                INSERT INTO "deribit.user_balance" (user_name, currency, balance, reserved)
                VALUES (:username, :currency, :balance, :reserved)
                ON CONFLICT(user_name, currency) DO UPDATE SET balance = :balance, reserved = :reserved
            """.trimIndent(),
            MapSqlParameterSource("username", balance.username)
                .addValue("currency", balance.currency)
                .addValue("balance", balance.balance)
                .addValue("reserved", balance.reserved),
        )
    }
}

