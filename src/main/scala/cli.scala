package scalex.cli

import dispatch._
import net.liftweb.json._

case class Parent(name: String, qualifiedName: String, typeParams: String)
case class Comment(body: Option[Body])
case class Body(txt: String, html: String)
case class Result(name: String, qualifiedName: String, parent: Parent, typeParams: String, 
                  valueParams: String, resultType: String, comment: Comment)

object ScalexCLI extends App {
  implicit val formats = DefaultFormats

  val http = new Http { 
    override def make_logger = new Logger { 
      def info(msg: String, items: Any*) = () 
      def warn(msg: String, items: Any*) = () 
    }
  }

  val query = (q: String) => {
    val req = :/("scalex.org", 8080) <<? Map("q" -> q)
    http(req >- JsonParser.parse)
  }

  val parse = (data: JValue) => (data \ "results").extract[List[Result]]

  val render = (results: List[Result]) => 
    results.map { r => green(r.parent.name) + " " + bold(r.name) + r.typeParams + 
                       (if (r.typeParams != "") ": " else "") + 
                       red(r.valueParams) + ": " + red(r.resultType) + "\n" + 
                       grey(r.qualifiedName) + "\n" + r.comment.body.map(_.txt).getOrElse("") }.mkString("\n\n")

  def red(s: String) = Console.RED + s + Console.RESET
  def green(s: String) = Console.GREEN + s + Console.RESET
  def bold(s: String) = Console.BOLD + s + Console.RESET
  def grey(s: String) = "\033[37m" + s + Console.RESET

  def help = { println(helpText); sys.exit(0) }

  def parseArgs(args: List[String], opts: Opts): Opts = args match {
    case Nil        => opts
    case "-h" :: as => help
    case "-d" :: as => parseArgs(as, opts.copy(detailedComments = true))
    case a :: as    => parseArgs(as, opts.copy(queries = opts.queries ++ List(a)))
  }

  parseArgs(args.toList, Opts(Nil)) match {
    case Opts(Nil, _) => println(red("Please provide a query"))
    case o            => o.queries.foreach(query andThen parse andThen render andThen println)
  }

  def helpText = """ FIXME """
}

case class Opts(queries: List[String], detailedComments: Boolean = false)
