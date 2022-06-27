package trabalhofinal.exceptions

class NumFungiNotSpecifiedException(numFungi: String): MapException("It seems your num fungi is incorectly specified or too large, num fungi: $numFungi")