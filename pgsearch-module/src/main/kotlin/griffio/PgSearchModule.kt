package griffio

import app.cash.sqldelight.dialect.api.IntermediateType
import app.cash.sqldelight.dialect.api.PrimitiveType
import app.cash.sqldelight.dialect.api.SqlDelightModule
import app.cash.sqldelight.dialect.api.TypeResolver
import app.cash.sqldelight.dialects.postgresql.PostgreSqlTypeResolver
import app.cash.sqldelight.dialects.postgresql.grammar.PostgreSqlParser
import app.cash.sqldelight.dialects.postgresql.grammar.PostgreSqlParserUtil
import com.alecstrong.sql.psi.core.SqlParser
import com.alecstrong.sql.psi.core.SqlParserUtil
import com.alecstrong.sql.psi.core.psi.SqlExpr
import com.alecstrong.sql.psi.core.psi.SqlFunctionExpr
import com.intellij.lang.parser.GeneratedParserUtilBase.Parser
import griffio.grammar.PgSearchParser
import griffio.grammar.PgSearchParserUtil
import griffio.grammar.PgSearchParserUtil.extension_expr
import griffio.grammar.PgSearchParserUtil.index_method
import griffio.grammar.PgSearchParserUtil.storage_parameters
import griffio.grammar.PgSearchParserUtil.type_name
import griffio.grammar.psi.PgSearchExtensionExpr

class PgSearchModule : SqlDelightModule {
    override fun typeResolver(parentResolver: TypeResolver): TypeResolver = PgSearchTypeResolver(parentResolver)

    override fun setup() {
        PgSearchParserUtil.reset()
        PgSearchParserUtil.overridePostgreSqlParser()
        // As the grammar doesn't support inheritance - override type_name manually to try inherited type_name
        // Capture any existing overrides (e.g., from other PostgreSql Modules)
        val previousTypeName = PostgreSqlParserUtil.type_name
        val previousExtensionExpr = PostgreSqlParserUtil.extension_expr
        val previousIndexMethod = PostgreSqlParserUtil.index_method
        val previousStorageParameters = PostgreSqlParserUtil.storage_parameters
        // etc
        PostgreSqlParserUtil.type_name = Parser { psiBuilder, i ->
            type_name?.parse(psiBuilder, i)
                    ?: PgSearchParser.type_name_real(psiBuilder, i)
                    || previousTypeName?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.type_name_real(psiBuilder, i)
        }

        PostgreSqlParserUtil.extension_expr = Parser { psiBuilder, i ->
            extension_expr?.parse(psiBuilder, i)
                    ?: PgSearchParser.extension_expr_real(psiBuilder, i)
                    || previousExtensionExpr?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.extension_expr_real(psiBuilder, i)
        }
        // etc
        PostgreSqlParserUtil.index_method = Parser { psiBuilder, i ->
            index_method?.parse(psiBuilder, i)
                    ?: PgSearchParser.index_method_real(psiBuilder, i)
                    || previousIndexMethod?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.index_method_real(psiBuilder, i)
        }
        // etc
        PostgreSqlParserUtil.storage_parameters = Parser { psiBuilder, i ->
            storage_parameters?.parse(psiBuilder, i)
                    ?: PgSearchParser.storage_parameters_real(psiBuilder, i)
                    || previousStorageParameters?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.storage_parameters_real(psiBuilder, i)
        }
    }
}

// Change to inheritance where some implementations may need to call `super` - not possible with delegation
// parentResolver is called to delegate to the next TypeResolver in the chain
private class PgSearchTypeResolver(private val parentResolver: TypeResolver) : PostgreSqlTypeResolver(parentResolver) {
    override fun resolvedType(expr: SqlExpr) : IntermediateType {
        return when (expr) {
            is PgSearchExtensionExpr if (expr.proximityOperatorExpression != null
                    || expr.matchBiJunctionOperatorExpression != null || expr.phraseOperatorExpression != null) -> IntermediateType(
                PrimitiveType.BOOLEAN
            )
            else -> parentResolver.resolvedType(expr)
        } // use parentResolver to use the module chain
    }

    override fun functionType(functionExpr: SqlFunctionExpr): IntermediateType? =
        when (functionExpr.functionName.text.lowercase()) {
            "score" -> IntermediateType(PrimitiveType.REAL)
            "snippet" -> IntermediateType(PrimitiveType.TEXT)
            "snippet_positions" -> IntermediateType(PrimitiveType.TEXT)
            else -> super.functionType(functionExpr) // postgresql.PostgreSqlTypeResolver.functionType calls parentResolver
        }
}
