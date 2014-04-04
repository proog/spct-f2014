using Microsoft.Owin.Hosting;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace SurfaceApp.Network
{
    public class ImageServer
    {
        public void Start()
        {
            string baseAddress = "http://localhost:9000/";
            // Start OWIN host 
            WebApp.Start<ImageServerConfig>(url: baseAddress);
        }
    }
}
