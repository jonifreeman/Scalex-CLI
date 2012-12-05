package scalex.cli

import dispatch._
import net.liftweb.json._

case class Parent(name: String, qualifiedName: String, typeParams: String)
case class Comment(short: Option[Text], body: Option[Text], valueParams: Option[Map[String, Text]])
case class Text(txt: String, html: String)
case class Result(name: String, qualifiedName: String, parent: Parent, typeParams: String, 
                  valueParams: String, resultType: String, comment: Option[Comment])

object ScalexCLI extends App {
  implicit val formats = DefaultFormats

  val http = new Http { 
    override def make_logger = new Logger { 
      def info(msg: String, items: Any*) = () 
      def warn(msg: String, items: Any*) = () 
    }
  }

  val query = (q: String) => {
    val req = :/("api.scalex.org", 80) <<? Map("q" -> q)
    http(req >- JsonParser.parse)
  }

  val parse = (data: JValue) => (data \ "results").extract[List[Result]]

  val render = (opts: Opts) => (results: List[Result]) => {
    def shortComment(r: Result) = (for { c <- r.comment; b <- c.short } yield b.txt).getOrElse("")
    def detailedComment(r: Result) = (for { c <- r.comment; b <- c.body } yield b.txt + "\n\n" + params(c)).getOrElse("")
    def params(c: Comment) = (c.valueParams.getOrElse(Map()).map {
      case (n, t) => "  " + bold(n) + (" " * ((20 - n.length) max 1)) + t.txt
    }).mkString("\n")

    results.map { r => green(r.parent.name) + " " + bold(r.name) + r.typeParams + 
                       (if (r.typeParams != "") ": " else "") + 
                       red(r.valueParams) + ": " + red(r.resultType) + "\n" + 
                       grey(r.qualifiedName) + "\n" + 
                       (if (opts.detailedComments) detailedComment(r) else shortComment(r)) }.mkString("\n\n")
  }

  def red    = Console.RED + (_: String) + Console.RESET
  def green  = Console.GREEN + (_: String) + Console.RESET
  def bold   = Console.BOLD + (_: String) + Console.RESET
  def grey   = "\033[37m" + (_: String) + Console.RESET

  def help = { println(helpText); sys.exit(0) }

  def parseArgs(args: List[String], opts: Opts): Opts = args match {
    case Nil        => opts
    case "-h" :: as => help
    case "-d" :: as => parseArgs(as, opts.copy(detailedComments = true))
    case a :: as    => parseArgs(as, opts.copy(queries = opts.queries ++ List(a)))
  }

  parseArgs(args.toList, Opts(Nil)) match {
    case Opts(Nil, _) => println(red("Please provide a query"))
    case opts         => opts.queries.foreach(query andThen parse andThen render(opts) andThen println)
  }

  def helpText = """|A command line interface to Scalex (scalex.org)
                    |
                    |  Usage: scalex [opts] q1 q2 ... qn
                    |
                    |  -h  -- prints this help
                    |  -d  -- prints detailed comments
                    |
                    |  Examples:
                    |
                    |  scalex 'List[A] => A'
                    |  scalex -d 'List[A] => A' 'List[A] => Option[A]'
                    |""".stripMargin
}

case class Opts(queries: List[String], detailedComments: Boolean = false)
