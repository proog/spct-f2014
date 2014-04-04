using System.Web.Http;
using System.Net.Http;
using System.IO;

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
            var resp = new HttpResponseMessage();
            resp.StatusCode = System.Net.HttpStatusCode.OK;
            resp.Content = new StreamContent(new FileStream("./Resources/icon.png", FileMode.Open));
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
