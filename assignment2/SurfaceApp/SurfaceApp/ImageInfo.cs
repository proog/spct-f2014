using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media.Imaging;
using System.Windows.Threading;

namespace SurfaceApp
{
    /// <summary>
    /// Contains metadata for an image.
    /// </summary>
    public class ImageInfo
    {
	    protected bool Equals(ImageInfo other) {
		    return OriginId == other.OriginId && string.Equals(FilePath, other.FilePath);
	    }

	    public override int GetHashCode() {
		    unchecked {
			    return (OriginId.GetHashCode()*397) ^ (FilePath != null ? FilePath.GetHashCode() : 0);
		    }
	    }

	    public ImageInfo(byte originId, string filePath) {
			FilePath = filePath;
			OriginId = originId;
		    var disp = Application.Current.Dispatcher;
		    disp.Invoke(() => LoadImage(filePath));
	    }

		public void DisposeImage() {
			
		}

		private void LoadImage(string filePath) {
			var u = new Uri(filePath);
			Image = new BitmapImage();
			Image.CacheOption = BitmapCacheOption.OnLoad;
			Image.StreamSource = new MemoryStream();
			var memStream = new MemoryStream();
			var fileStream = File.Open(filePath, FileMode.Open);
			fileStream.CopyTo(memStream);
			memStream.Seek(0, SeekOrigin.Begin);
			fileStream.Close();

			Image.BeginInit();
			Image.StreamSource = memStream;
			Image.EndInit();
		}

        /// <summary>
        /// Get or set the ID of the device that provided this image.
        /// </summary>
        public byte OriginId { get; private set; }

		public string FilePath { get; private set; }

		public BitmapImage Image { get; private set; }

		public override bool Equals(object obj) {
			if (ReferenceEquals(null, obj)) return false;
			if (ReferenceEquals(this, obj)) return true;
			if (obj.GetType() != this.GetType()) return false;
			return Equals((ImageInfo) obj);
		}
    }
}
