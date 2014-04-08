using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Threading;

namespace SurfaceApp
{
    public class ScatterViewViewModel
    {
        public static readonly string IMG_DIR = System.AppDomain.CurrentDomain.SetupInformation.ApplicationBase + ConfigurationManager.AppSettings["img_upload_dir"];
        public ObservableCollection<string> Images { get; private set; }

        private readonly Dispatcher disp;

        public ScatterViewViewModel()
        {
            disp = Dispatcher.CurrentDispatcher;
            Images = new ObservableCollection<string>();
            foreach(var path in GetImagePaths(IMG_DIR))
            {
                Images.Add(path);
            }
        }

        public void AddImage(string path)
        {
            disp.Invoke(() => Images.Add(path));
        }

        public void RemoveImage(string path)
        {
            disp.Invoke(() => Images.Remove(path));
        }

        /// <summary>
        /// TODO make tail recursive?
        /// </summary>
        /// <param name="rootPath"></param>
        /// <returns></returns>
        public IEnumerable<string> GetImagePaths(string rootPath)
        {
            var paths = new List<string>();
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
