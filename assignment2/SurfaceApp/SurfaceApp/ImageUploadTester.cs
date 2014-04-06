using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace SurfaceApp.Network
{
    public class ImageUploadTester
    {
        private BackgroundWorker worker = new BackgroundWorker();

        private static readonly string TEST_IMG_FILE_NAME;

        static ImageUploadTester()
        {
            TEST_IMG_FILE_NAME = ConfigurationManager.AppSettings["test_img_filename"];
            InitTestDir();
        }

        public ImageUploadTester()
        {
            worker.DoWork += new DoWorkEventHandler(
                    delegate(object o, DoWorkEventArgs args)
                    {
                        WebClient client = new WebClient();
                        string url = ImageServer.ServerBaseAddress + "/images/1337";
                        client.UploadFile(address: url, method: "POST", fileName: TestDir + TEST_IMG_FILE_NAME);
                    }
                );
        }

        public void StartUpload()
        {
            this.worker.RunWorkerAsync();
        }

        public static string TestDir
        {
            get
            {
                string appDir = System.AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
                return appDir + ConfigurationManager.AppSettings["test_dir"];
            }
        }

        private static void InitTestDir()
        {
            // Create directory if not yet initialized.
            if (!Directory.Exists(TestDir))
            {
                Directory.CreateDirectory(TestDir);
            }
        }
    }
}
