package griffio

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import griffio.queries.Sample
import org.postgresql.ds.PGSimpleDataSource

private fun getSqlDriver() = PGSimpleDataSource().apply {
    setURL("jdbc:postgresql://localhost:5432/mydatabase")
    applicationName = "App Main"
    user = "myuser"
    password = "mypassword"
}.asJdbcDriver()


fun main() {
    val driver = getSqlDriver()
    val sample = Sample(driver)

    sample.searchQueries.selectMixedNumericString().executeAsList().forEach { println(it) }
}
