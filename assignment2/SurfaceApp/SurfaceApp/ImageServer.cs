using Microsoft.Owin.Hosting;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace SurfaceApp.Network
{
    /// <summary>
    /// Simple utility class used to start the REST service.
    /// 
    /// </summary>
    public class ImageServer
    {
        /// <summary>
        /// The singleton instance.
        /// </summary>
        private static readonly ImageServer singleton = new ImageServer();
        private bool started = false;
        private ReaderWriterLockSlim rwLock = new ReaderWriterLockSlim();

        static ImageServer()
        {
            InitUploadDir();
        }

        /// <summary>
        /// Make constructor private as we want the ImageServer to be a singleton.
        /// </summary>
        private ImageServer()
        {

        }

        /// <summary>
        /// Get the ImageServer singleton.
        /// </summary>
        /// <returns>The ImageServer singleton instance.</returns>
        public static ImageServer GetInstance()
        {
            return singleton;
            //try
            //{
            //    rwLock.EnterUpgradeableReadLock();
            //    // Do we need to initalize singleton?
            //    if (ReferenceEquals(singleton, null))
            //    {
            //        try
            //        {
            //            rwLock.EnterWriteLock();
            //            // Initialize upload directory if it does not already exist.
            //            InitUploadDir();
            //            singleton = new ImageServer();

            //        }
            //        finally
            //        {
            //            rwLock.ExitWriteLock();
            //        }
            //    }
            //    return singleton;
            //}
            //finally
            //{
            //    rwLock.ExitUpgradeableReadLock();
            //}
        }

        /// <summary>
        /// Starts the image server.
        /// </summary>
        /// <exception cref="InvalidOperationException">If the ImageServer has already been started.</exception>
        public void Start()
        {
            try
            {
                rwLock.EnterWriteLock();
                if (started)
                {
                    throw new InvalidOperationException(this.GetType().ToString() + " cannot be started twice.");
                }
                else
                {
                    started = true;
                    // Start OWIN host 
                    WebApp.Start<ImageServerConfig>(url: ServerBaseAddress);
                }
            }
            finally
            {
                rwLock.ExitWriteLock();
            }
        }

        /// <summary>
        /// Get the base address of the REST image server.
        /// </summary>
        public static string ServerBaseAddress
        {
            get
            {
                return ConfigurationManager.AppSettings["server_address"];
            }
        }

        /// <summary>
        /// Get the physical path of where uploaded images are stored.
        /// </summary>
        public static string ImageUploadPath
        {
            get
            {
                string appDir = System.AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
                return appDir + ConfigurationManager.AppSettings["img_upload_dir"];
            }
        }

        /// <summary>
        /// Initializes the image upload directory if it doesn't exist.
        /// </summary>
        private static void InitUploadDir()
        {
            // Create directory if not yet initialized.
            if (!Directory.Exists(ImageUploadPath))
            {
                Directory.CreateDirectory(ImageUploadPath);
            }
        }
    }
}
