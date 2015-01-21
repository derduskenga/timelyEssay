import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

object GlobalWeb extends GlobalSettings {	
	
	// 404 - page not found error
	override def onHandlerNotFound (request: RequestHeader) = {
		play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
		var arrays = request.uri.split("/").toList.contains("mydashboard");
		if(arrays)
			Future.successful(NotFound(views.html.web.errors.dashboardOnHandlerNotFound(request)))
		else
			Future.successful(NotFound(views.html.web.errors.onHandlerNotFound(request)))
	}
	
	// 500 - internal server error
	override def onError (request: RequestHeader, throwable: Throwable) = {
		play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
		var arrays = request.uri.split("/").toList.contains("mydashboard");
		if(arrays)
			Future.successful(InternalServerError(views.html.web.errors.dashboardOnError(throwable)))
		else	
			Future.successful(InternalServerError(views.html.web.errors.onError(throwable))	)
	}
	
	// called when a route is found, but it was not possible to bind the request parameters
	override def onBadRequest (request: RequestHeader, error: String) = {
		var arrays = request.uri.split("/").toList.contains("mydashboard");
		play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
		if(arrays)
			Future.successful(BadRequest(views.html.web.errors.dashboardbadrequest.render()));
		else
		Future.successful(BadRequest(views.html.web.errors.webbadrequest.render()))
	}
}