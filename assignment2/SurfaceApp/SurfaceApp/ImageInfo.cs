using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
		}

        /// <summary>
        /// Get or set the ID of the device that provided this image.
        /// </summary>
        public byte OriginId { get; private set; }

		public string FilePath { get; private set; }

		public override bool Equals(object obj) {
			if (ReferenceEquals(null, obj)) return false;
			if (ReferenceEquals(this, obj)) return true;
			if (obj.GetType() != this.GetType()) return false;
			return Equals((ImageInfo) obj);
		}
    }
}
