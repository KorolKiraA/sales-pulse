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

  def main(args: Array[String]): Unit =
    val file = args.headOption.getOrElse("demo-sales.csv")
    if args.isEmpty then generateDemo(file)

    val sales = Using(Source.fromFile(file, "UTF-8")) { src =>
      src.getLines().drop(1).flatMap(parse).toVector
    }.getOrElse(Vector.empty)

    if sales.isEmpty then
      println("Нет данных. Проверьте формат CSV: date;product;category;qty;price")
      return

    val totalRevenue = sales.map(_.revenue).sum
    val avgCheck = totalRevenue / sales.size

    println(s"=== SalesPulse v1.0 — отчёт по файлу $file ===")
    println(f"Продаж: ${sales.size}, Выручка: $totalRevenue%,.2f, Средний чек: $avgCheck%,.2f\n")

    section("Топ-5 товаров по выручке"):
      sales.groupBy(_.product).view.mapValues(_.map(_.revenue).sum)

    section("Топ-5 категорий"):
      sales.groupBy(_.category).view.mapValues(_.map(_.revenue).sum)

    println("--- Динамика по месяцам ---")
    val byMonth = sales.groupBy(_.month).view.mapValues(_.map(_.revenue).sum).toSeq.sortBy(_._1)
    val max = byMonth.map(_._2).max
    byMonth.foreach { (m, r) =>
      val bar = "█" * (40.0 * r / max).toInt
      println(f"  $m | $bar $r%,.0f")
    }

    // ABC-анализ: какие товары дают 80% выручки
    println("\n--- ABC-анализ (класс A = 80% выручки) ---")
    val sorted = sales.groupBy(_.product).view.mapValues(_.map(_.revenue).sum)
      .toSeq.sortBy(-_._2)
    var cum = 0.0
    var classA = 0
    while cum < 0.8 * totalRevenue && classA < sorted.size do
      cum += sorted(classA)._2
      classA += 1
    println(f"  Класс A: $classA товаров из ${sorted.size} (${100.0 * classA / sorted.size}%.0f%% ассортимента)")

  def section(title: String)(data: scala.collection.MapView[String, Double]): Unit =
    println(s"--- $title ---")
    data.toSeq.sortBy(-_._2).take(5).zipWithIndex.foreach { case ((k, v), i) =>
      println(f"  ${i + 1}. $k%-20s $v%,12.2f")
    }
    println()

  def parse(line: String): Option[Sale] =
    line.split(";") match
      case Array(d, p, c, q, pr) =>
        Try(Sale(d.trim, p.trim, c.trim, q.trim.toInt,
                 pr.trim.replace(",", ".").toDouble)).toOption
      case _ => None

  def generateDemo(file: String): Unit =
    val rnd = new scala.util.Random(42)
    val products = Map(
      "Ноутбук Pro 15" -> "Электроника", "Монитор 27\"" -> "Электроника",
      "Клавиатура MX" -> "Аксессуары", "Мышь Silent" -> "Аксессуары",
      "Кресло Ergo" -> "Мебель", "Стол Loft" -> "Мебель",
      "Наушники ANC" -> "Электроника", "Коврик XL" -> "Аксессуары")
    val prices = Map("Ноутбук Pro 15" -> 129990.0, "Монитор 27\"" -> 34990.0,
      "Клавиатура MX" -> 12990.0, "Мышь Silent" -> 5990.0,
      "Кресло Ergo" -> 45990.0, "Стол Loft" -> 28990.0,
      "Наушники ANC" -> 24990.0, "Коврик XL" -> 1990.0)
    val pw = new PrintWriter(file, "UTF-8")
    pw.println("date;product;category;qty;price")
    for i <- 1 to 500 do
      val p = products.keys.toVector(rnd.nextInt(products.size))
      val month = 1 + rnd.nextInt(10)
      val day = 1 + rnd.nextInt(28)
      pw.println(f"2026-$month%02d-$day%02d;$p;${products(p)};${1 + rnd.nextInt(5)};${prices(p)}%.0f")
    pw.close()
    println(s"Сгенерирован демо-файл $file (500 продаж)\n")
