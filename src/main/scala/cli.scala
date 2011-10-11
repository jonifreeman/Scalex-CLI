package scalex.cli

import dispatch._
import net.liftweb.json._

case class Parent(name: String, qualifiedName: String, typeParams: String)
case class Comment(text: String, html: String)
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
    val req = :/("scalex.org", 8080) / "scalex-http" <<? Map("q" -> q)
    http(req >- JsonParser.parse)
  }

  val parse = (data: JValue) => (data \ "results").extract[List[Result]]

  val render = (results: List[Result]) => 
    results.map { r => green(r.parent.name) + " " + bold(r.name) + r.typeParams + 
                       (if (r.typeParams != "") ": " else "") + 
                       red(r.valueParams) + ": " + red(r.resultType) + "\n" + 
                       grey(r.qualifiedName) + "\n" + r.comment.text }.mkString("\n\n")

  def red(s: String) = Console.RED + s + Console.RESET
  def green(s: String) = Console.GREEN + s + Console.RESET
  def bold(s: String) = Console.BOLD + s + Console.RESET
  def grey(s: String) = "\033[37m" + s + Console.RESET

  println(args.headOption.map(query andThen parse andThen render).getOrElse(red("Please provide a query")))
}
