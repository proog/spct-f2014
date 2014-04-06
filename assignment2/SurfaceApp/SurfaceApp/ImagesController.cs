using System.Web.Http;
using System.Net.Http;
using System.IO;
using System.Configuration;
using System.Drawing;
using System.Net;
using System.Threading.Tasks;
using System;

namespace SurfaceApp.Network
{
    public class ImagesController : ApiController
    {

        /// <summary>
        /// Get a specific image.
        /// </summary>
        /// <param name="id">The ID of the image to be downloaded.</param>
        /// <returns>A response containing the image as payload, or an error response if no image was found for the given id.</returns>
        public HttpResponseMessage Get(string id)
        {
            var resp = new HttpResponseMessage();
            var imgPath = ImageServer.ImageUploadPath + id;
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

        public HttpResponseMessage Post()
        {
            if (!Request.Content.IsMimeMultipartContent())
            {
                throw new HttpResponseException(HttpStatusCode.UnsupportedMediaType);
            }

            var provider = new MultipartMemoryStreamProvider();

            var task = Request.Content.ReadAsMultipartAsync(provider);
            task.Wait();
            foreach (var file in provider.Contents)
            {
                var fileName = file.Headers.ContentDisposition.FileName.Trim('\"');
                Task<byte[]> readBuffer = file.ReadAsByteArrayAsync();
                readBuffer.Wait();
                byte[] buffer = readBuffer.Result;
                ImageServer.GetInstance().SaveImage(Image.FromStream(new MemoryStream(buffer)));
            }

            return Request.CreateResponse(HttpStatusCode.OK);
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
