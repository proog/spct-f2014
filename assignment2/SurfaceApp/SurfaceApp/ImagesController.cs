using System.Web.Http;
using System.Net.Http;
using System.IO;
using System.Web;

namespace SurfaceApp.Network
{
    public class ImagesController : ApiController
    {

        /// <summary>
        /// Get a specific image.
        /// </summary>
        /// <param name="id">The ID of the image to be downloaded.</param>
        /// <returns></returns>
        public HttpResponseMessage Get(string id)
        {
            //string appDir = System.AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
            var resp = new HttpResponseMessage();
            //var virPath = System.Web.Hosting.HostingEnvironment.ApplicationVirtualPath;
            //var physPath = System.Web.Hosting.HostingEnvironment.ApplicationPhysicalPath;
            //var mappedPath = System.Web.Hosting.HostingEnvironment.MapPath("~/Resources");
            //string imgPath = appDir + "uploads\\images\\" + id;

            string imgPath = "/Resources/uploads/images/" + id;
            var found = File.Exists(imgPath);
            if (found)
            {
                resp.StatusCode = System.Net.HttpStatusCode.OK;
                resp.Content = new StreamContent(new FileStream(imgPath, FileMode.Open));
            }
            else
            {
                resp.StatusCode = System.Net.HttpStatusCode.NotFound;
                resp.Content = new StringContent("No image with the provided ID.");
            }
            return resp;
        }

        // POST api/values 
        public void Post([FromBody]string value)
        {
        }

        // PUT api/values/5 
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5 
        public void Delete(int id)
        {

        }

    }
}
