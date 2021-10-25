package co.copper.deribit.storage

import co.copper.deribit.model.UserBalance
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class UserBalanceRowMapper : RowMapper<UserBalance> {
    override fun mapRow(rs: ResultSet, rowNum: Int): UserBalance? = UserBalance(
        rs.getString("user_name"),
        rs.getString("currency"),
        rs.getBigDecimal("balance"),
        rs.getBigDecimal("reserved")
    )
}