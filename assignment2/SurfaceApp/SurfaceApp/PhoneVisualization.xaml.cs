using System.Windows.Controls.Primitives;
using Microsoft.Surface.Presentation;
using Microsoft.Surface.Presentation.Controls;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Microsoft.Surface.Presentation.Controls.Primitives;

namespace SurfaceApp
{
    /// <summary>
    /// Interaction logic for PhoneVisualization.xaml
    /// </summary>
    public partial class PhoneVisualization : TagVisualization {
		public bool Pinned { get; private set; }
		public byte DeviceId { get; set; }

        public string GetBtnPinToolTip()
        {
            if (_btnPin.IsChecked.HasValue && _btnPin.IsChecked.Value)
                return "Unpin phone";
            else
                return "Pin phone";
        }

        public PhoneVisualization() {
	        InitializeComponent();
        }

		private void PinButtonClick(object sender, RoutedEventArgs e) {
			var tb = sender as ToggleButton;

			Pinned = tb.IsChecked.HasValue && tb.IsChecked.Value;
		}

		private void Rectangle_Drop_1(object sender, SurfaceDragDropEventArgs e) {
			Console.WriteLine(e.Cursor.Data as string);

			var imageInfo = e.Cursor.Data as ImageInfo;
			var split = imageInfo.FilePath.Split(new[] { "\\images\\" + imageInfo.OriginId + "\\" }, StringSplitOptions.RemoveEmptyEntries);
			var url = "/images/" + imageInfo.OriginId + "/" + split[split.Length - 1];
			SignalR.GetInstance().RequestImageDownloadToPhone(DeviceId, url, split[split.Length - 1]);
		}

        private void BtnPin_Checked(object sender, RoutedEventArgs e)
        {
            var stb = sender as SurfaceToggleButton;
            if (stb.IsChecked.HasValue && stb.IsChecked.Value)
            {
                this.TagRemovedBehavior = TagRemovedBehavior.Persist;
                this._btnPin.Content = GetBtnPinToolTip();
            }
                
        }

        private void BtnPin_Unchecked(object sender, RoutedEventArgs e)
        {
            var stb = sender as SurfaceToggleButton;
            if (stb.IsChecked.HasValue && !stb.IsChecked.Value)
            {
                this.TagRemovedBehavior = TagRemovedBehavior.Fade;
                this._btnPin.Content = GetBtnPinToolTip();
            }
        }
    }
}
