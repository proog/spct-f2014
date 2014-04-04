using System.Web.Http;
using System.Net.Http;
using System.IO;
using System.Configuration;

namespace SurfaceApp.Network
{
    public class ImagesController : ApiController
    {

        public ImagesController()
        {
            this.InitUploadDir();
        }

        /// <summary>
        /// Get a specific image.
        /// </summary>
        /// <param name="id">The ID of the image to be downloaded.</param>
        /// <returns>A response containing the image as payload, or an error response if no image was found for the given id.</returns>
        public HttpResponseMessage Get(string id)
        {
            var resp = new HttpResponseMessage();
            var imgPath = this.ImageUploadPath + id;
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

        private void InitUploadDir()
        {

            // Create directory if not yet initialized.
            if (!Directory.Exists(this.ImageUploadPath))
            {
                Directory.CreateDirectory(this.ImageUploadPath);
            }

        }

        private string ImageUploadPath
        {
            get
            {
                string appDir = System.AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
                string upDir = appDir + ConfigurationManager.AppSettings["img_upload_dir"];
                return upDir;
            }

        }

    }
}
