import scala.io.Source
import scala.util.{Try, Using}
import java.io.PrintWriter
import scala.collection.mutable

/**
 * SalesPulse — аналитика продаж из CSV-выгрузки (1С, CRM, Excel-экспорт).
 *
 * Коммерческая ценность: мгновенный отчёт для руководителя продаж без
 * Power BI и Excel — выручка, средний чек, топ товаров и категорий,
 * динамика по месяцам. Читает CSV формата: date;product;category;qty;price
 *
 * Запуск:  scala-cli run SalesPulse.scala
 *      или scala-cli run SalesPulse.scala -- sales.csv
 * Без аргумента генерирует демо-данные на 500 продаж.
 */
object SalesPulse:

  case class Sale(date: String, product: String, category: String,
                  qty: Int, price: Double):
    def revenue: Double = qty * price
    def month: String = date.take(7) // "2026-03"
