# SalesPulse

![Scala](https://img.shields.io/badge/Scala-3-red) ![License](https://img.shields.io/badge/license-MIT-blue) ![Dependencies](https://img.shields.io/badge/dependencies-none-brightgreen)

Instant sales analytics from a CSV export (1C, CRM, Excel).
Gives a sales lead an executive-grade report without Power BI or Excel:
revenue, average check, top products and categories, monthly dynamics and
**ABC analysis** — which 20% of the assortment drives 80% of revenue.

## Features

- Parses CSV in `date;product;category;qty;price` format
- Top-5 products and categories by revenue
- Monthly revenue histogram in the terminal
- ABC analysis (class A = 80% of revenue)
- Generates a 500-sale demo dataset when no file is given

## Quick start

Requires **Scala 3** via [scala-cli](https://scala-cli.virtuslab.org/):

```bash
scala-cli run SalesPulse.scala              # demo dataset
scala-cli run SalesPulse.scala -- sales.csv # your data
```

## Sample output

```
--- ABC analysis (class A = 80% of revenue) ---
  Class A: 3 products of 8 (38% of assortment)

--- Monthly dynamics ---
  2026-03 | ████████████████████████████████████████ 1 245 000
  2026-07 | ██████████████ 438 000
```

## Tech stack

Scala 3 · collections · case classes · standard library only.

## License

MIT — see [LICENSE](LICENSE).
