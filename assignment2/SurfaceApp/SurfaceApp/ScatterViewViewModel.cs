using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Threading;
using SurfaceApp.Network;

namespace SurfaceApp
{
    public class ScatterViewViewModel
    {
        public static readonly string IMG_DIR = System.AppDomain.CurrentDomain.SetupInformation.ApplicationBase + ConfigurationManager.AppSettings["img_upload_dir"];
        public ObservableCollection<ImageInfo> Images { get; private set; }

        private readonly Dispatcher disp;

        public ScatterViewViewModel()
        {
            disp = Dispatcher.CurrentDispatcher;
            Images = new ObservableCollection<ImageInfo>();
			foreach(var path in GetImagePaths(IMG_DIR))
			{
				Images.Add(new ImageInfo(19, path));
			}

			SignalR.GetInstance().DeviceDisconnected += OnDeviceDisconnected;
			ImageServer.GetInstance().ImageAdded += OnImageAdded;
        }

	    private void OnImageAdded(byte b, string s) {
		    var info = new ImageInfo(b, s);
			disp.Invoke(() => AddImage(info));
	    }

	    private void OnDeviceDisconnected(byte b) {
		    disp.Invoke(() => RemoveImages(b));
	    }

		private void RemoveImages(byte b) {
			var paths = GetImagePaths(IMG_DIR + b);

			if(paths.Count == 0)
				return;

			foreach(var path in paths) {
				var info = new ImageInfo(b, path);
				RemoveImage(info);
			}

			//Directory.Delete(IMG_DIR + b, true);
		}

	    private void AddImage(ImageInfo info)
        {
            Images.Add(info);
        }

		private void RemoveImage(ImageInfo info)
        {
            Images.Remove(info);
        }

        /// <summary>
        /// TODO make tail recursive?
        /// </summary>
        /// <param name="rootPath"></param>
        /// <returns></returns>
        public List<string> GetImagePaths(string rootPath)
        {
            var paths = new List<string>();

			if(!Directory.Exists(rootPath))
				return paths;

            paths.AddRange(Directory.GetFiles(rootPath));
            if (Directory.GetDirectories(rootPath).Length == 0)
            {
                // If no sub dirs we are done.
                return paths;
            }
            foreach (var dir in Directory.GetDirectories(rootPath))
            {
                paths.AddRange(GetImagePaths(dir));
            }
            return paths;
        }
    }
}
