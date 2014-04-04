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

        /// <summary>
        /// Get or set the server side ID of this image.
        /// </summary>
        public long Id { get; set; }

        /// <summary>
        /// Get or set the ID of the device that provided this image.
        /// </summary>
        public String originId { get; set; }

        public string FileName {
            get
            {
                // TODO extension needed?
                return this.Id.ToString() + ".image";
            }
        }
    }
}
